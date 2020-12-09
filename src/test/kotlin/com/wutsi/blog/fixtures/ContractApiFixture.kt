package com.wutsi.blog.fixtures

import com.wutsi.blog.client.payment.ContractDto
import com.wutsi.blog.client.payment.GetContractResponse
import org.apache.commons.lang.time.DateUtils
import java.util.Date

object ContractApiFixture {
    fun createContractDto(userId: Long) = ContractDto(
        id = System.currentTimeMillis(),
        userId = userId,
        startDate = DateUtils.addMonths(Date(), -10),
        endDate = DateUtils.addMonths(Date(), 10)
    )

    fun createGetContractResponse(userId: Long) = GetContractResponse(
        contract = createContractDto(userId)
    )
}
