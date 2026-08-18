from fastapi import FastAPI, HTTPException
from uuid import UUID

from app.storage import eta_results
from app.kafka_consumer import run_consumer_in_background


app = FastAPI(
    title="ETA Service",
    version="1.0.0"
)


@app.on_event("startup")
def startup():
    run_consumer_in_background()


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
            detail=f"ETA not found for shipment: {shipment_id}"
        )

    return eta