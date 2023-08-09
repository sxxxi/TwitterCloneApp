package ca.sxxxi.titter.data.models

data class Page<T> (
	val content: T,
	val isLast: Boolean,
 	val page: Int,
	val size: Int,
	val totalPage: Int,
	val pageSize: Int
)
