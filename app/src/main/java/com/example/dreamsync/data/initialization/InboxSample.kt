package com.example.dreamsync.data.initialization

import com.example.dreamsync.data.models.FriendInvite
import com.example.dreamsync.data.models.HikeInvite

object InboxSample {
    val inboxSamples = listOf(
        FriendInvite(
            id = "1",
            senderName = "Alice",
            status = "Pending",
            timestamp = "2025-01-13 10:30"
        ),
        FriendInvite(
            id = "2",
            senderName = "Bob",
            status = "Accepted",
            timestamp = "2025-01-12 14:15"
        ),
        HikeInvite(
            id = "3",
            hikeName = "Dream Journey to Paris",
            senderName = "Charlie",
            status = "Pending",
            timestamp = "2025-01-11 09:45"
        ),
        HikeInvite(
            id = "4",
            hikeName = "Subconscious Safari",
            senderName = "Dave",
            status = "Declined",
            timestamp = "2025-01-10 18:00"
        )
    )
}
