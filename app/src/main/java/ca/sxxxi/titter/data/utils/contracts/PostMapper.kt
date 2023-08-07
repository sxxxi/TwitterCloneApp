package ca.sxxxi.titter.data.utils.contracts

import ca.sxxxi.titter.data.local.entities.combine.PostWithUser
import ca.sxxxi.titter.data.models.Post
import ca.sxxxi.titter.data.network.models.PostNM

interface PostMapper : Mapper<PostNM, PostWithUser, Post>
