package io.micronaut.nats.docs.consumer.queue

import io.micronaut.context.annotation.Requires
// tag::imports[]
import io.micronaut.nats.annotation.NatsClient
import io.micronaut.nats.annotation.Subject
// end::imports[]

@Requires(property = "spec.name", value = "QueueSpec")
// tag::clazz[]
@NatsClient
interface ProductClient {

    @Subject("product") // <1>
    fun send(data: ByteArray)

}
// end::clazz[]
