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

        val list = arrayListOf<TaxEntity>()

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
                in 1..3 -> BigDecimal(10000)
                in 4..12 -> BigDecimal(10000)
                else -> throw Exception("薪资月份不正确")
            }

            // 1月
            val taxEntity = TaxEntity(month, salaryEveryMonth, socialSecurity, payPercentage, BigDecimal(2500)).apply {
                // 当前月累计算税收入
                val taxRateSalary =
                    // 当前月累计月工资
                    (list.sumOf { it.salary } + salary) -
                            // 当前月累计政策减免(起征点)
                            BigDecimal(5000 * month) -
                            // 当前月累计专项扣除（五险一金）
                            (socialSecurityAmount.payAndSocial + list.sumOf { it.socialSecurityAmount.payAndSocial }) -
                            // 当前月累计专项附加扣除（租房...）
                            (specialDeduction + list.sumOf { it.specialDeduction })

                // 适应税率
                val taxRate = taxRate(taxRateSalary)

                // 个税
                personalIncomeTax =
                    (taxRateSalary * taxRate.rate - BigDecimal(taxRate.div) - list.sumOf { it.personalIncomeTax })
                        .setScale(2, RoundingMode.HALF_DOWN)

                // 税后工资
                postTaxSalary = preTaxSalary - personalIncomeTax
            }
            list.add(taxEntity)
            println(taxEntity)
        }
    }

    /**
     * 根据税前工资=月薪-五险一金，获取对应的税率
     * 单位 100
     */
    private fun taxRate(prePayTax: BigDecimal): TaxRate {
        return when {
            prePayTax <= 36000.toBigDecimal() -> TaxRate(BigDecimal("0.03"), 0)
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

// 个人所得税税率表一
// 级数 累计预扣预缴应纳税所得额 税率 速算扣除数
// 1 不超过36000元的 3% 0
// 2 超过36000元至144000元的部分 10% 2520
// 3 超过144000元至300000元的部分 20% 16920
// 4 超过300000元至420000元的部分 25% 31920
// 5 超过420000元至660000元的部分 30% 52920
// 6 超过660000元至960000元的部分 35% 85920
// 7 超过960000的部分 45% 181920
// 累计预扣法的计算公式：
// 本期应预扣预缴税额=（累计预扣预缴应纳税所得额×税率 - 速算扣除数 ）- 累计减免税额 - 累计已预扣预缴税额
// 累计预扣预缴应纳税所得额=累计收入-累计减免收入- 累计基本减除费用 - 累计专项扣除 -累计专项附加扣除-累计依法确定的其他扣除
// 其中，累计基本减除费用，按照5000元/月乘以纳税人当年在本单位的任职受雇工作月份数计算。

// 将取得的年终奖总额除以12个月，按其商数在按月换算后的综合所得税率表中确定适用税率和速算扣除数，单独计算纳税。
// 年终奖应纳税额＝年终奖总额×适用税率－速算扣除数
