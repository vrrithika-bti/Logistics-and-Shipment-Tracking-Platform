from typing import Dict
from uuid import UUID

from app.models import EtaResponse, LocationUpdate


latest_locations: Dict[UUID, LocationUpdate] = {}

eta_results: Dict[UUID, EtaResponse] = {}