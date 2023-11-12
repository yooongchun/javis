package zoz.cool.javis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import zoz.cool.javis.common.exception.Asserts;
import zoz.cool.javis.domain.OmsAccountBalance;
import zoz.cool.javis.service.OmsAccountBalanceService;
import zoz.cool.javis.mapper.OmsAccountBalanceMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author zhayongchun
 * @description 针对表【oms_account_balance(订单表)】的数据库操作Service实现
 * @createDate 2023-11-30 22:45:18
 */
@Service
public class OmsAccountBalanceServiceImpl extends ServiceImpl<OmsAccountBalanceMapper, OmsAccountBalance>
        implements OmsAccountBalanceService {

    public boolean deductBalance(Long userId, BigDecimal amount) {
        OmsAccountBalance account = getOne(new QueryWrapper<OmsAccountBalance>().eq("user_id", userId));
        Asserts.failIfNull(account, "账户不存在");
        account.setBalance(account.getBalance().subtract(amount));
        baseMapper.updateById(account);
        return true;
    }
}




