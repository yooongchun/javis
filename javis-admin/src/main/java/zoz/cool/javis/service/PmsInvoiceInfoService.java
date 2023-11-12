package zoz.cool.javis.service;

import org.springframework.scheduling.annotation.Async;
import zoz.cool.javis.domain.PmsInvoiceInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import zoz.cool.javis.dto.FileDTO;

/**
 * @author zhayongchun
 * @description 针对表【pms_invoice_info(发票信息表)】的数据库操作Service
 * @createDate 2023-11-30 22:45:18
 */
public interface PmsInvoiceInfoService extends IService<PmsInvoiceInfo> {

    PmsInvoiceInfo upload(FileDTO file);
    @Async("inv")
    void asyncGetInvoiceInfo(FileDTO file, PmsInvoiceInfo invoiceInfo);
}
