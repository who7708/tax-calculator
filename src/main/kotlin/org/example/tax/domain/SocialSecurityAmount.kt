package org.example.tax.domain

import java.math.BigDecimal

/**
 * 社保缴费
 *
 * @author Chris
 * @date 2021/07/03
 * @since 1.0.0
 */
data class SocialSecurityAmount(
    val salary: BigDecimal,
    var socialSecurityBase: SocialSecurityBase,
    var payPercentage: PayPercentage,
) {
    /**
     * 养老保险 = 缴费基数 * 比例， 向上取保留1位小数
     */
    val pension: BigDecimal
        get() {
            return payPercentage.pension.multiply(getPayLimit(salary))
                .setScale(1, BigDecimal.ROUND_UP)
        }

    /**
     * 医疗保险+生育保险（已合并） = 缴费基数 * 比例， 向上取保留1位小数
     * 公司 10%，个人 2%
     */
    val medicalCare: BigDecimal
        get() = payPercentage.medicalCare.multiply(getPayLimit(salary))
            .setScale(1, BigDecimal.ROUND_UP)

    /**
     * 失业保险 = 缴费基数 * 比例， 向上取保留1位小数
     * 公司 0.5%，个人 0.5%
     */
    val unemployment: BigDecimal
        get() = payPercentage.unemployment.multiply(getPayLimit(salary))
            .setScale(1, BigDecimal.ROUND_UP)

    /**
     * 公积金 = 缴费基数 * 比例， 向下取整
     *
     * 公司 1-7%，个人 1-7%
     */
    val fund: BigDecimal
        get() = payPercentage.fund.multiply(getPayLimit(salary))
            .setScale(0, BigDecimal.ROUND_HALF_DOWN)

    /**
     * 补充公积金 = 缴费基数 * 比例， 向下取整
     *
     * 公司 1-5%，个人 1-5%
     */
    val supplyFund: BigDecimal
        get() = payPercentage.supplyFund.multiply(getPayLimit(salary))
            .setScale(0, BigDecimal.ROUND_HALF_DOWN)

    /**
     * 五险一金总费用
     */
    val payAndSocial: BigDecimal
        get() = pension.add(medicalCare).add(unemployment).add(fund).add(supplyFund)

    private fun getPayLimit(salary: BigDecimal): BigDecimal {
        return when {
            salary <= socialSecurityBase.lowerLimit -> socialSecurityBase.lowerLimit
            socialSecurityBase.lowerLimit < salary && salary <= socialSecurityBase.upperLimit -> salary
            salary >= socialSecurityBase.upperLimit -> socialSecurityBase.upperLimit
            else -> throw Exception("月工资错误")
        }
    }

    override fun toString(): String {
        return "SocialSecurityAmount(payPercentage=$payPercentage, pension=$pension, medicalCare=$medicalCare, unemployment=$unemployment, fund=$fund, supplyFund=$supplyFund, payAndSocial=$payAndSocial)"
    }
}