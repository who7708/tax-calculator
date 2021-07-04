package org.example.tax

import org.example.tax.domain.PayPercentage
import org.example.tax.domain.SocialSecurityBase
import org.example.tax.domain.TaxEntity
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @author Chris
 * @version 1.0
 * @date 2021/7/3
 */
class TestTax {
    @Test
    fun test1() {
        // 2020上海社保月缴费基数上下限如下：
        val socialSecurity2020 = SocialSecurityBase(BigDecimal(28017), BigDecimal(4927), BigDecimal(9339))

        // 2021上海社保月缴费基数上下限如下
        val socialSecurity2021 = SocialSecurityBase(BigDecimal(31014), BigDecimal(5975), BigDecimal(10338))

        // 社保缴费比例
        val payPercentage =
            PayPercentage(BigDecimal("0.08"), BigDecimal("0.02"), BigDecimal("0.005"), BigDecimal("0.07"))

        (1..12).forEach { month ->
            // println("${month}月")

            // 社保基数
            val socialSecurity = when (month) {
                in 1..6 -> socialSecurity2020
                in 7..12 -> socialSecurity2021
                else -> throw Exception("社保基数月份不正确")
            }

            // 月薪
            val salaryEveryMonth = when (month) {
                in 1..3 -> BigDecimal(100)
                in 4..12 -> BigDecimal(200)
                else -> throw Exception("薪资月份不正确")
            }

            // 1月
            val taxEntity = TaxEntity(month, salaryEveryMonth, socialSecurity, payPercentage).apply {
                // socialSecurityBase = socialSecurity
                // socialSecurityAmount = SocialSecurityAmount(socialSecurityBase, payPercentage)
                /*.apply {
                    // 养老保险
                    pension = payPercentage.pension.multiply(socialSecurityBase.upperLimit)
                        .setScale(1, BigDecimal.ROUND_UP)

                    // 医疗保险
                    medicalCare = payPercentage.medicalCare.multiply(socialSecurityBase.upperLimit)
                        .setScale(1, BigDecimal.ROUND_UP)

                    // 失业保险
                    unemployment = payPercentage.unemployment.multiply(socialSecurityBase.upperLimit)
                        .setScale(1, BigDecimal.ROUND_UP)

                    // 公积金
                    fund = payPercentage.fund.multiply(socialSecurityBase.upperLimit)
                        .setScale(0, BigDecimal.ROUND_HALF_DOWN)

                    // 补充公积金
                    supplyFund = payPercentage.supplyFund.multiply(socialSecurityBase.upperLimit)
                        .setScale(0, BigDecimal.ROUND_HALF_DOWN)
                }*/

                // 税前
                preTaxSalary = salary.minus(this.socialSecurityAmount.payAndSocial)

                // 专项扣除
                specialDeduction = 2500

                // 个税
                personalIncomeTax =
                    (preTaxSalary.minus(specialDeduction.toBigDecimal())
                        .minus(5000.toBigDecimal())) * BigDecimal("0.03")
                        .setScale(2, RoundingMode.HALF_DOWN)

                // 税后工资=tu
                postTaxSalary = preTaxSalary - personalIncomeTax
            }
            println(taxEntity)
        }
    }

    /**
     * 根据税前工资=月薪-五险一金，获取对应的税率
     * 单位 100
     */
    private fun taxRate(prePayTax: BigDecimal): TaxRate {
        return when {
            prePayTax <= 36000.toBigDecimal() -> TaxRate(BigDecimal("0.0"), 0)
            36000.toBigDecimal() < prePayTax && prePayTax <= 144000.toBigDecimal() -> TaxRate(BigDecimal("0.1"), 2520)
            144000.toBigDecimal() < prePayTax && prePayTax <= 300000.toBigDecimal() -> TaxRate(BigDecimal("0.2"), 16920)
            300000.toBigDecimal() < prePayTax && prePayTax <= 420000.toBigDecimal() -> TaxRate(BigDecimal("0.25"),
                31920)
            420000.toBigDecimal() < prePayTax && prePayTax <= 660000.toBigDecimal() -> TaxRate(BigDecimal("0.3"), 52920)
            660000.toBigDecimal() < prePayTax && prePayTax <= 960000.toBigDecimal() -> TaxRate(BigDecimal("0.35"),
                85920)
            else -> TaxRate(BigDecimal("0.45"), 181920)
        }
    }
}

data class TaxRate(
    /**
     * 税率
     */
    val rate: BigDecimal = BigDecimal("0.0"),

    /**
     * 速算扣除数
     */
    val div: Int = 0,
)