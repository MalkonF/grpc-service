package br.com.zup.edu

import com.google.protobuf.Any
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FretesGrpcServer : FretesServiceGrpc.FretesServiceImplBase() {
    private val logger = LoggerFactory.getLogger(FretesServiceGrpc::class.java)

    override fun calculaFrete(request: CalculaFreteRequest?, responseObserver: StreamObserver<CalculaFreteResponse>?) {
        logger.info("Calculando frete para request: $request")

        val cep = request?.cep
        if (cep == null || cep.isBlank()) {
            val error = Status.INVALID_ARGUMENT
                .withDescription("Cep deve ser informado")
                .asRuntimeException()
            responseObserver?.onError(error)
            return
        }

        if (!cep!!.matches("[0-9]{5}-[\\d]{3}".toRegex())) {
            responseObserver?.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("Cep no formato inválido.")
                    .augmentDescription("Formato esperado deve ser 99999-999")
                    .asRuntimeException()
            )
            return
        }

        //simula um erro de segurança qualquer. Claro que cep com final 333 n é um erro de segurança,
        //mas foi feito só para dar um exemplo de como tratar este tipo de erro de segurança
        if (cep.endsWith("333")) {
            val statusProto: com.google.rpc.Status = com.google.rpc.Status.newBuilder()
                .setCode(Code.PERMISSION_DENIED.getNumber())
                .setMessage("Sem permissão p acessar esse recurso")
                .addDetails(
                    Any.pack(
                        ErrorDetails.newBuilder()
                            .setCode(401)
                            .setMessage("Token expirado.")
                            .build()
                    )
                )
                .build()
            responseObserver?.onError(StatusProto.toStatusRuntimeException(statusProto))
            return
        }

        var valor = 0.0
        try {
            valor = Random.nextDouble(from = 0.0, until = 140.0)
            if (valor >= 100.0) throw IllegalStateException("Testando captura de erro... Estado inesperado!")
        } catch (e: Exception) {
            responseObserver?.onError(
                Status.INTERNAL
                    .withDescription(e.message)
                    .withCause(e)
                    .asRuntimeException()
            )
        }

        val response = CalculaFreteResponse.newBuilder()
            .setCep(request!!.cep)
            .setValor(valor)
            .build()
        logger.info("Frete calculado: $response")
        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}