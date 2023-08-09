package ca.sxxxi.titter.data.utils.mappers

import ca.sxxxi.titter.data.models.Page
import ca.sxxxi.titter.data.network.models.responses.PagedResponse
import ca.sxxxi.titter.data.utils.contracts.NDMapper
import ca.sxxxi.titter.data.utils.contracts.PageMapper

class PageMapperImpl<N, D>(private val nToDMapper: NDMapper<N, D>) : PageMapper<N, D> {
	override fun networkToDomain(net: PagedResponse<N>): Page<D> {
		return Page(
			content = nToDMapper.networkToDomain(net.content),
			isLast = net.last,
			page = net.pageable.pageNumber,
			size = net.totalElements,
			totalPage = net.totalPages,
			pageSize = net.pageable.pageSize
		)
	}
}