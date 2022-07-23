package com.eneskacan.bankingsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("logs")
public class Log {
    @Id @JsonIgnore
    private long id;
    private final String message;
}
