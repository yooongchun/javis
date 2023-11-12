package zoz.cool.javis.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import zoz.cool.javis.client.BaiduInvClient;
import zoz.cool.javis.common.constants.InvStatus;
import zoz.cool.javis.common.service.MinioService;
import zoz.cool.javis.common.util.FileUtil;
import zoz.cool.javis.domain.PmsInvoiceInfo;
import zoz.cool.javis.dto.FileDTO;
import zoz.cool.javis.service.OmsAccountBalanceService;
import zoz.cool.javis.service.OmsProductPriceService;
import zoz.cool.javis.service.PmsInvoiceInfoService;
import zoz.cool.javis.mapper.PmsInvoiceInfoMapper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author zhayongchun
 * @description 针对表【pms_invoice_info】的数据库操作Service实现
 * @createDate 2023-11-30 22:45:18
 */
@Service
@Slf4j
public class PmsInvoiceInfoServiceImpl extends ServiceImpl<PmsInvoiceInfoMapper, PmsInvoiceInfo> implements PmsInvoiceInfoService {

    private final MinioService minioService;

    private static final String bucketName = "invoice";
    private final BaiduInvClient baiduInvClient;

    private final OmsAccountBalanceService accountBalanceService;

    private final BigDecimal invParsePrice;

    @Autowired
    public PmsInvoiceInfoServiceImpl(MinioService minioService, BaiduInvClient baiduInvClient, OmsAccountBalanceService accountBalanceService, OmsProductPriceService productPriceService) {
        this.minioService = minioService;
        this.baiduInvClient = baiduInvClient;
        this.accountBalanceService = accountBalanceService;
        this.invParsePrice = productPriceService.getById(1).getPrice();
    }

    @Override
    public PmsInvoiceInfo upload(FileDTO file) {
        // 上传至MinIO
        String dateStr = DateUtil.format(LocalDateTime.now(), "yyyy-MM-dd");
        String userId = StpUtil.getLoginIdAsString();
        String uuid = UUID.randomUUID().toString();
        String objectName = dateStr + "/" + "user_" + userId + "/" + uuid + "/" + file.getFileName();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes());
        log.info("上传文件: bucket: {}, object: {}", bucketName, objectName);
        minioService.uploadFile(bucketName, objectName, inputStream);

        // 保存至数据库
        PmsInvoiceInfo invoiceInfo = new PmsInvoiceInfo();
        invoiceInfo.setUserId(Long.valueOf(userId));
        invoiceInfo.setFileName(file.getFileName());
        invoiceInfo.setFileHash(FileUtil.calculateFileHash(file.getBytes()));
        invoiceInfo.setFileType(file.getContentType());
        invoiceInfo.setBucketName(bucketName);
        invoiceInfo.setObjectName(objectName);
        invoiceInfo.setStatus(InvStatus.INIT.getCode());
        save(invoiceInfo);
        return invoiceInfo;
    }

    @Override
    public void asyncGetInvoiceInfo(FileDTO file, PmsInvoiceInfo invoiceInfo) {
        log.info("[BaiduInvClient] 异步获取发票信息: id={}", invoiceInfo.getId());
        invoiceInfo.setStatus(InvStatus.PROCESSING.getCode());
        invoiceInfo.setMethod("BaiduInvClient");
        updateById(invoiceInfo);

        JsonNode node = null;
        try {
            node = baiduInvClient.getInvoiceInfo(file);
        } catch (Exception e) {
            log.error("[BaiduInvClient] 获取发票信息失败", e);
            invoiceInfo.setStatus(InvStatus.FAIL.getCode());
            invoiceInfo.setRemark("[BaiduInvClient]: 获取发票信息失败 err=" + e.getMessage());
            updateById(invoiceInfo);
        }
        if (node != null) {
            invoiceInfo.setInvCode(node.get("InvoiceCode").asText());
            invoiceInfo.setInvNum(node.get("InvoiceNum").asText());
            invoiceInfo.setInvChk(node.get("CheckCode").asText());
            invoiceInfo.setInvDate(DateUtil.parse(node.get("InvoiceDate").asText(), "yyyy年MM月dd日"));
            invoiceInfo.setInvMoney(BigDecimal.valueOf(node.get("TotalAmount").asDouble()));
            invoiceInfo.setInvTax(node.get("TotalTax").asText());
            invoiceInfo.setInvTotal(node.get("AmountInFiguers").asText());
            invoiceInfo.setInvType(node.get("InvoiceType").asText());
            invoiceInfo.setInvDetail(node.toString());
            invoiceInfo.setStatus(InvStatus.SUCCESS.getCode());
            invoiceInfo.setRemark("[BaiduInvClient]: 获取发票信息成功");

            updateById(invoiceInfo);
            log.info("[BaiduInvClient] 获取发票信息成功: id={}", invoiceInfo.getId());
            // 扣除费用
            accountBalanceService.deductBalance(invoiceInfo.getUserId(), invParsePrice);
            log.info("[BaiduInvClient] 扣除费用成功: id={}, price={}", invoiceInfo.getId(), invParsePrice);
        } else {
            invoiceInfo.setStatus(InvStatus.FAIL.getCode());
            invoiceInfo.setRemark("[BaiduInvClient]: 获取发票信息失败");
            updateById(invoiceInfo);
            log.error("[BaiduInvClient] 获取发票信息失败，node is null");
        }
    }
}
