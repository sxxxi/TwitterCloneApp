package ca.sxxxi.titter.data.utils.contracts

import ca.sxxxi.titter.data.models.Comment
import ca.sxxxi.titter.data.network.models.CommentNM

interface CommentMapper : NDMapper<CommentNM, Comment>