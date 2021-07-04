package org.example.tax.domain

import java.math.BigDecimal

/**
 * @author Chris
 * @version 1.0
 * @date 2021/7/3
 */
class TaxEntity(
    /**
     * 当前月份，1-12
     */
    var month: Int,
    /**
     * 月薪
     */
    var salary: BigDecimal,
    /**
     * 社保基数
     */
    var socialSecurityBase: SocialSecurityBase,
    /**
     * 社保缴费比例
     */
    var payPercentage: PayPercentage,
    /**
     * 专项扣除
     */
    var specialDeduction: BigDecimal = BigDecimal(0),
) {

    /**
     * 五险一金缴费
     */
    var socialSecurityAmount: SocialSecurityAmount = SocialSecurityAmount(salary, socialSecurityBase, payPercentage)

    /**
     * 税前工资=月薪-五险一金-每月减免（目前是5000）
     */
    var preTaxSalary = salary.minus(this.socialSecurityAmount.payAndSocial)

    /**
     * 个人所得税
     */
    var personalIncomeTax = BigDecimal("0.0")

    /**
     * 税后工资
     */
    var postTaxSalary = BigDecimal("0.0")

    override fun toString(): String {
        return "TaxEntity(month=$month, salary=$salary, socialSecurityBase=$socialSecurityBase, payPercentage=$payPercentage, socialSecurityAmount=$socialSecurityAmount, preTaxSalary=$preTaxSalary, specialDeduction=$specialDeduction, personalIncomeTax=$personalIncomeTax, postTaxSalary=$postTaxSalary)"
    }

}