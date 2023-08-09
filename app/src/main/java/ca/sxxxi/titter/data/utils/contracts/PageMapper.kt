package ca.sxxxi.titter.data.utils.contracts

import ca.sxxxi.titter.data.models.Page
import ca.sxxxi.titter.data.network.models.responses.PagedResponse

interface PageMapper<N, D> : NDMapper<PagedResponse<N>, Page<D>>