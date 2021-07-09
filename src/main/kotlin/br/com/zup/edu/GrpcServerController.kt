package br.com.zup.edu

import io.micronaut.grpc.server.GrpcEmbeddedServer
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller
class GrpcServerController(val grpcServer: GrpcEmbeddedServer) {
    @Get("/grpc/stop")
    fun stop(): HttpResponse<String> {
        grpcServer.stop()//derruba server grpc
        return HttpResponse.ok("${grpcServer.isRunning}")
    }
}