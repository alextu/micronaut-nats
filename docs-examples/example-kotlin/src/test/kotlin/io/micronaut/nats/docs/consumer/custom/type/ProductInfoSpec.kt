package io.micronaut.nats.docs.consumer.custom.type

import io.kotest.assertions.timing.eventually
import io.kotest.matchers.collections.shouldExist
import io.kotest.matchers.shouldBe
import io.micronaut.nats.AbstractNatsTest
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ProductInfoSpec : AbstractNatsTest({

    val specName = javaClass.simpleName

    given("A custom type binder") {
        val ctx = startContext(specName)

        `when`("The messages are published") {
            val productListener = ctx.getBean(ProductListener::class.java)

            // tag::producer[]
            val productClient = ctx.getBean(ProductClient::class.java)
            productClient.send("body".toByteArray())
            productClient.send("medium", 20L, "body2".toByteArray())
            productClient.send(null, 30L, "body3".toByteArray())
            // end::producer[]

            then("The messages are received") {
                eventually(Duration.seconds(10)) {
                    productListener.messages.size shouldBe 3
                    productListener.messages shouldExist { p -> p.count == 10L && p.sealed }
                    productListener.messages shouldExist { p -> p.count == 20L && p.sealed }
                    productListener.messages shouldExist { p -> p.count == 30L && p.sealed }
                }
            }
        }

        ctx.stop()
    }
})
