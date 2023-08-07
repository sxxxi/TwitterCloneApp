package ca.sxxxi.titter.data.utils.contracts

interface Mapper<N, E, D> {
	fun networkToEntity(source: N): E
	fun entityToDomain(source: E): D
}

