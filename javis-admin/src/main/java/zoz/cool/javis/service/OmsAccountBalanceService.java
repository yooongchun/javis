package zoz.cool.javis.service;

import zoz.cool.javis.domain.OmsAccountBalance;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
* @author zhayongchun
* @description 针对表【oms_account_balance(订单表)】的数据库操作Service
* @createDate 2023-11-30 22:45:18
*/
public interface OmsAccountBalanceService extends IService<OmsAccountBalance> {

    boolean deductBalance(Long userId, BigDecimal amount);

}
