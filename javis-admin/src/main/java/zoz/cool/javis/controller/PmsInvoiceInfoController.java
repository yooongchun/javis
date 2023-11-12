package zoz.cool.javis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zoz.cool.javis.common.api.CommonPage;
import zoz.cool.javis.common.api.CommonResult;
import zoz.cool.javis.common.constants.InvStatus;
import zoz.cool.javis.common.exception.ApiException;
import zoz.cool.javis.common.exception.Asserts;
import zoz.cool.javis.common.service.MinioService;
import zoz.cool.javis.common.util.FileUtil;
import zoz.cool.javis.domain.OmsAccountBalance;
import zoz.cool.javis.domain.PmsInvoiceInfo;
import zoz.cool.javis.dto.FileDTO;
import zoz.cool.javis.dto.request.UpdateInvFields;
import zoz.cool.javis.service.OmsAccountBalanceService;
import zoz.cool.javis.service.OmsProductPriceService;
import zoz.cool.javis.service.PmsInvoiceInfoService;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@Api(tags = "PmsInvoiceInfoController")
@Tag(name = "PmsInvoiceInfoController", description = "发票信息管理")
@Slf4j
@RequestMapping("/inv")
@SaCheckLogin
public class PmsInvoiceInfoController {
    private final PmsInvoiceInfoService invoiceInfoService;

    private final MinioService minioService;
    private final BigDecimal invParsePrice;
    private final OmsAccountBalanceService accountBalanceService;
    private final List<String> supportedMimeType = Arrays.asList("image/png", "image/jpeg", "image/bmp", "image/webp", "application/pdf");

    @Autowired
    public PmsInvoiceInfoController(PmsInvoiceInfoService invoiceInfoService, MinioService minioService, OmsProductPriceService productPriceService, OmsAccountBalanceService accountBalanceService) {
        this.invoiceInfoService = invoiceInfoService;
        this.minioService = minioService;
        this.invParsePrice = productPriceService.getById(1).getPrice();
        this.accountBalanceService = accountBalanceService;
    }

    @ApiOperation("添加发票信息")
    @PostMapping("/upload")
    @ResponseBody
    public CommonResult<PmsInvoiceInfo> create(@RequestParam("file") MultipartFile file) {
        // 文件对象
        FileDTO fileDTO = new FileDTO(file.getOriginalFilename(), file.getContentType());
        // 检查文件类型
        if (fileDTO.getContentType() == null || !supportedMimeType.contains(fileDTO.getContentType())) {
            log.warn("不支持的文件类型:{}", fileDTO.getContentType());
            throw new ApiException("不支持的文件类型:" + fileDTO.getContentType());
        }
        // 读取bytes
        try {
            fileDTO.setBytes(file.getBytes());
        } catch (IOException e) {
            log.error("读取文件失败", e);
            throw new ApiException("读取文件失败:" + e.getMessage());
        }
        String fileHash = FileUtil.calculateFileHash(fileDTO.getBytes());
        PmsInvoiceInfo invInfo = invoiceInfoService.getOne(new QueryWrapper<PmsInvoiceInfo>()
                .eq("file_hash", fileHash).eq("user_id", StpUtil.getLoginIdAsLong()));
        // 文件不存在则上传
        if (invInfo == null) {
            invInfo = invoiceInfoService.upload(fileDTO);
        }
        // 文件第一次上传或者上传失败的情况下才进行识别
        if (invInfo.getStatus() == InvStatus.INIT.getCode() || invInfo.getStatus() == InvStatus.FAIL.getCode()) {
            // 先判断当前余额是否足够
            OmsAccountBalance account = accountBalanceService.getOne(new QueryWrapper<OmsAccountBalance>().eq("user_id", StpUtil.getLoginIdAsLong()));
            Asserts.failIfNull(account, "用户账户不存在");
            if (account.getBalance().compareTo(invParsePrice) < 0) {
                throw new ApiException("余额不足");
            }
            // 异步调用
            invoiceInfoService.asyncGetInvoiceInfo(fileDTO, invInfo);
        }
        return CommonResult.success(invInfo);
    }

    @ApiOperation("重试解析发票信息")
    @PostMapping("/retry/{id}")
    @ResponseBody
    public CommonResult<Boolean> retry(@PathVariable Long id) {
        PmsInvoiceInfo invInfo = invoiceInfoService.getById(id);
        Asserts.failIfNull(invInfo, "发票信息不存在");

        log.info("下载文件: bucket: {}, object: {}", invInfo.getBucketName(), invInfo.getObjectName());
        InputStream input = minioService.getObject(invInfo.getBucketName(), invInfo.getObjectName());
        Asserts.failIfNull(input, "MinIO: 文件不存在");
        // 异步调用
        log.info("重试解析: {}", invInfo.getId());
        FileDTO file = new FileDTO(invInfo.getFileName(), invInfo.getFileType());
        invoiceInfoService.asyncGetInvoiceInfo(file, invInfo);

        return CommonResult.success(true);
    }

    @ApiOperation("删除发票信息")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        PmsInvoiceInfo invInfo = invoiceInfoService.getById(id);
        Asserts.failIfNull(invInfo, "发票信息不存在");
        invInfo.setIsDelete(true);
        invoiceInfoService.updateById(invInfo);
        return CommonResult.success(true);
    }

    @ApiOperation("获取发票信息")
    @GetMapping("/{id}")
    @ResponseBody
    public CommonResult<PmsInvoiceInfo> get(@PathVariable Long id) {
        PmsInvoiceInfo invInfo = invoiceInfoService.getById(id);
        Asserts.failIfNull(invInfo, "发票信息不存在");
        return CommonResult.success(invInfo);
    }

    @ApiOperation("获取发票信息列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<CommonPage<PmsInvoiceInfo>> list(@RequestParam(value = "invCode", required = false) String invCode,
                                                         @RequestParam(value = "invNum", required = false) String invNum,
                                                         @RequestParam(value = "invChk", required = false) String invChk,
                                                         @RequestParam(value = "invDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date invDate,
                                                         @RequestParam(value = "invMoney", required = false) BigDecimal invMoney,
                                                         @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<PmsInvoiceInfo> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PmsInvoiceInfo> query = new QueryWrapper<>();
        if (!StpUtil.hasRole("admin")) {
            query.eq("user_id", StpUtil.getLoginIdAsLong());
        }
        if (StrUtil.isNotEmpty(invCode)) {
            query.eq("inv_code", invCode);
        }
        if (StrUtil.isNotEmpty(invNum)) {
            query.eq("inv_num", invNum);
        }
        if (StrUtil.isNotEmpty(invChk)) {
            query.like("inv_chk", invChk);
        }
        if (invMoney != null) {
            query.eq("inv_money", invMoney);
        }
        if (invDate != null) {
            query.eq("inv_date", invDate);
        }
        Page<PmsInvoiceInfo> invList = invoiceInfoService.page(page, query);
        return CommonResult.success(CommonPage.restPage(invList));
    }

    @ApiOperation("更新发票信息")
    @PostMapping("/update/{id}")
    @SaCheckLogin
    @ResponseBody
    public CommonResult<PmsInvoiceInfo> update(@PathVariable Long id, @RequestBody UpdateInvFields fields) {
        PmsInvoiceInfo invInfo = invoiceInfoService.getById(id);
        Asserts.failIfNull(invInfo, "发票信息不存在");
        // 更新字段
        BeanUtil.copyProperties(fields, invInfo, CopyOptions.create().setIgnoreNullValue(true));
        return CommonResult.success(invInfo);
    }
}
