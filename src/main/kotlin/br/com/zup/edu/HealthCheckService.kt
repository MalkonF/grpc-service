package br.com.zup.edu

import io.grpc.health.v1.HealthCheckRequest
import io.grpc.health.v1.HealthCheckResponse
import io.grpc.health.v1.HealthGrpc
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton//check vc pode checar varios serviços da aplicação. Mas pra isso tem que implementar aqui
//uma maneira de checar se os serviços estao up ou down, tipo um ping etc
class HealthCheckService : HealthGrpc.HealthImplBase() {
    override fun check(request: HealthCheckRequest?, responseObserver: StreamObserver<HealthCheckResponse>?) {
        responseObserver?.onNext(
            HealthCheckResponse.newBuilder()
                .setStatus(HealthCheckResponse.ServingStatus.SERVING).build()
        )
        responseObserver?.onCompleted()
    }

    //aqui ao inves de checar de tempos em tempos ele abre um canal de comunicação continuo com servidor,
    //aí assim que o status de algo mudar o grpc vai notificar atraves desse canal e quem estiver escutando
    //vai saber
    //
    override fun watch(request: HealthCheckRequest?, responseObserver: StreamObserver<HealthCheckResponse>?) {
        responseObserver?.onNext(
            HealthCheckResponse.newBuilder()
                .setStatus(HealthCheckResponse.ServingStatus.SERVING).build()
        )
    }
}