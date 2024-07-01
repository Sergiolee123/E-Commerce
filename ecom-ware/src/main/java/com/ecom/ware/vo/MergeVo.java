package com.ecom.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class MergeVo {
    private Long id;
    private List<Long> items;
}
