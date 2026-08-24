from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from uuid import UUID

from app.kafka_consumer import start_kafka_consumer
from app.storage import eta_results


app = FastAPI(
    title="ETA Service",
    version="1.0.0"
)


app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:5173",
        "http://127.0.0.1:5173",
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.on_event("startup")
def startup_event():
    print(
        "ETA Service startup event triggered",
        flush=True
    )

    start_kafka_consumer()

    print(
        "ETA Kafka consumer thread started",
        flush=True
    )


@app.get("/actuator/health")
def health():
    return {
        "status": "UP",
        "service": "eta-service"
    }


@app.get("/api/v1/eta/{shipment_id}")
def get_eta(shipment_id: UUID):

    eta = eta_results.get(shipment_id)

    if eta is None:
        raise HTTPException(
            status_code=404,
            detail=(
                f"ETA not found for shipment: "
                f"{shipment_id}"
            )
        )

    return eta