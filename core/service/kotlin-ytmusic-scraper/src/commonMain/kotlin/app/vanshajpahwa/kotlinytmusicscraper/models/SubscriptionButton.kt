package app.vanshajpahwa.kotlinytmusicscraper.models

import app.vanshajpahwa.kotlinytmusicscraper.models.subscriptionButton.SubscribeButtonRenderer
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionButton(
    val subscribeButtonRenderer: SubscribeButtonRenderer,
)