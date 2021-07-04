package org.example.tax.domain

import java.math.BigDecimal

/**
 * 社保缴费比例
 *
 * @author Chris
 * @version 1.0
 * @date 2021/7/3
 */
data class PayPercentage(
    /**
     * 养老保险
     * 公司 16%，个人 8%
     */
    var pension: BigDecimal,

    /**
     * 医疗保险+生育保险（已合并）
     * 公司 10%，个人 2%
     */
    var medicalCare: BigDecimal,

    /**
     * 失业保险
     * 公司 0.5%，个人 0.5%
     */
    var unemployment: BigDecimal,

    /**
     * 公积金 公司 1-7%，个人 1-7%
     */
    var fund: BigDecimal,

    /**
     * 补充公积金 公司 1-5%，个人 1-5%
     */
    var supplyFund: BigDecimal = BigDecimal("0.0"),

    /**
     * 工伤保险
     * 公司 0.16-1.5%，个人 无
     */
    var injury: BigDecimal = BigDecimal("0.0"),
)

// 社保缴费比例            公司	个人	合计
// 养老保险               0.16	0.08	0.24
// 医疗保险+生育保险(已合并) 0.1	0.02	0.12
// 失业保险               0.005	0.005	0.01
// 工伤保险               0.16%-1.52%	/	0.0016
// 合计                  0.2716	0.105	0.3766