package com.eneskacan.bankingsystem.mybatis;

import com.eneskacan.bankingsystem.model.Log;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LogMapper {
    boolean save(String message);
    List<Log> getAll();
}
