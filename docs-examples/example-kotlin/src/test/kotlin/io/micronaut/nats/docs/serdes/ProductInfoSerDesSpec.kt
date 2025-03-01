package io.micronaut.nats.docs.serdes

import io.kotest.assertions.timing.eventually
import io.kotest.matchers.collections.shouldExist
import io.kotest.matchers.shouldBe
import io.micronaut.nats.AbstractNatsTest
import io.micronaut.nats.docs.serdes.ProductClient
import io.micronaut.nats.docs.serdes.ProductListener
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ProductInfoSerDesSpec: AbstractNatsTest({

    val specName = javaClass.simpleName

    given("A basic producer and consumer") {
        val ctx = startContext(specName)

        `when`("the message is published") {
            val listener = ctx.getBean(ProductListener::class.java)

// tag::producer[]
            val productClient = ctx.getBean(ProductClient::class.java)
            productClient.send(ProductInfo("small", 10L, true))
            productClient.send(ProductInfo("medium", 20L, true))
            productClient.send(ProductInfo(null, 30L, false))
// end::producer[]

            then("the message is consumed") {
                eventually(Duration.seconds(10)) {
                    listener.messages.size shouldBe 3
                    listener.messages shouldExist { p -> p.size == "small" && p.count == 10L && p.sealed }
                    listener.messages shouldExist { p -> p.size == "medium" && p.count == 20L && p.sealed }
                    listener.messages shouldExist { p -> p.size == null && p.count == 30L && !p.sealed }
                }
            }
        }

        ctx.stop()
    }
})
