package org.example.tax.domain

import java.math.BigDecimal

/**
 * 社保缴费基数
 *
 * @author Chris
 * @version 1.0
 * @date 2021/7/3
 */
data class SocialSecurityBase(
    /**
     * 缴费上限
     */
    val upperLimit: BigDecimal,

    /**
     * 缴费下限
     */
    val lowerLimit: BigDecimal,

    /**
     * 缴费基数
     */
    val payBase: BigDecimal,
)