package com.eneskacan.bankingsystem.mybatis;

import com.eneskacan.bankingsystem.model.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper {
    void save(Account account);
    Account findById(long id);
    boolean update(Account account);
}
