package com.ecom.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ecom.common.valid.anno.ListValue;
import com.ecom.common.valid.group.CreateGroup;
import com.ecom.common.valid.group.UpdateStatusGroup;
import com.ecom.common.valid.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 品牌
 * 
 * @author Sergiolee123
 * @email lee.sergio.hk@gmail.com
 * @date 2024-03-11 21:09:17
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@NotNull(message = "update must assign a brand Id", groups = {UpdateGroup.class, UpdateStatusGroup.class})
	@Null(message = "create cannot assign brand Id", groups = {CreateGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */

	@NotBlank(message = "Brand name cannot be empty", groups = {CreateGroup.class, UpdateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(groups = {CreateGroup.class})
	@URL(message = "logo must be a valid url address", groups = {CreateGroup.class, UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(groups = {CreateGroup.class, UpdateStatusGroup.class})
	@ListValue(vals = {0, 1}, groups = {CreateGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotBlank(groups = {CreateGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$", message = "value must be a letter", groups = {CreateGroup.class, UpdateGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull
	@Min(value = 0, message = "sorting must >= 0")
	private Integer sort;

}
