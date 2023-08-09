package ca.sxxxi.titter.data.utils.contracts

import ca.sxxxi.titter.data.models.UserSearchItem
import ca.sxxxi.titter.data.network.models.responses.UserSearchResult

interface UserSearchMapper : NDMapper<List<UserSearchResult>, List<UserSearchItem>>