package ca.sxxxi.titter.data.network.models.responses

import com.squareup.moshi.Json

data class PagedResponse<T>(
	@field:Json(name = "content") val content: T,
	@field:Json(name = "first") val first: Boolean,
	@field:Json(name = "last") val last: Boolean,
	@field:Json(name = "size") val size: Int,
	@field:Json(name = "totalPages") val totalPages: Int,
	@field:Json(name = "numberOfElements") val numberOfElements: Int,
	@field:Json(name = "totalElements") val totalElements: Int,
	@field:Json(name = "empty") val empty: Boolean,
	@field:Json(name = "pageable") val pageable: PageInfo,
) {
	data class PageInfo(
		val offset: Int,
		val pageNumber: Int,
		val pageSize: Int
	)
}

/*
"pageable": {
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "offset": 1,
        "pageNumber": 1,
        "pageSize": 1,
        "paged": true,
        "unpaged": false
    }
 */
