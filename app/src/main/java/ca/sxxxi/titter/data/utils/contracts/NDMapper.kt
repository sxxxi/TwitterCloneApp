package ca.sxxxi.titter.data.utils.contracts

interface NDMapper<N, D> {
	fun networkToDomain(net: N): D
}