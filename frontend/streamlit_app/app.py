import streamlit as st
import requests

from services.shipment_api import get_shipments


# ---------------------------------------------------------
# Page configuration
# ---------------------------------------------------------

st.set_page_config(
    page_title="Logistics & Shipment Tracking Platform",
    page_icon="🚚",
    layout="wide"
)


# ---------------------------------------------------------
# Header
# ---------------------------------------------------------

st.title("🚚 Logistics & Shipment Tracking Platform")

st.subheader("Real-Time Logistics Dashboard")

st.write(
    "Monitor shipment activity and current shipment status."
)

st.divider()


# ---------------------------------------------------------
# Load shipment data
# ---------------------------------------------------------

try:

    with st.spinner("Loading shipment data..."):

        data = get_shipments(
            page=0,
            size=100
        )

    shipments = data.get(
        "content",
        []
    )

    total_shipments = data.get(
        "totalElements",
        len(shipments)
    )

    # -----------------------------------------------------
    # Calculate statistics
    # -----------------------------------------------------

    created_count = sum(
        1
        for shipment in shipments
        if shipment.get("status") == "CREATED"
    )

    picked_up_count = sum(
        1
        for shipment in shipments
        if shipment.get("status") == "PICKED_UP"
    )

    in_transit_count = sum(
        1
        for shipment in shipments
        if shipment.get("status") == "IN_TRANSIT"
    )

    out_for_delivery_count = sum(
        1
        for shipment in shipments
        if shipment.get("status") == "OUT_FOR_DELIVERY"
    )

    delivered_count = sum(
        1
        for shipment in shipments
        if shipment.get("status") == "DELIVERED"
    )

    cancelled_count = sum(
        1
        for shipment in shipments
        if shipment.get("status") == "CANCELLED"
    )


    # -----------------------------------------------------
    # Dashboard metrics
    # -----------------------------------------------------

    col1, col2, col3, col4 = st.columns(4)

    with col1:

        st.metric(
            label="Total Shipments",
            value=total_shipments
        )

    with col2:

        st.metric(
            label="Created",
            value=created_count
        )

    with col3:

        st.metric(
            label="In Transit",
            value=in_transit_count
        )

    with col4:

        st.metric(
            label="Delivered",
            value=delivered_count
        )


    st.divider()


    # -----------------------------------------------------
    # Additional status information
    # -----------------------------------------------------

    st.subheader("📊 Shipment Status")

    col1, col2, col3 = st.columns(3)

    with col1:

        st.metric(
            label="Picked Up",
            value=picked_up_count
        )

    with col2:

        st.metric(
            label="Out for Delivery",
            value=out_for_delivery_count
        )

    with col3:

        st.metric(
            label="Cancelled",
            value=cancelled_count
        )


    st.divider()


    # -----------------------------------------------------
    # Recent shipments
    # -----------------------------------------------------

    st.subheader("📦 Recent Shipments")


    if not shipments:

        st.info(
            "No shipments have been created yet."
        )

    else:

        for shipment in shipments[:10]:

            with st.container(border=True):

                col1, col2, col3, col4 = st.columns(4)

                with col1:

                    st.write(
                        "**Tracking Number**"
                    )

                    st.write(
                        shipment.get(
                            "trackingNumber",
                            "N/A"
                        )
                    )

                with col2:

                    st.write(
                        "**Status**"
                    )

                    st.write(
                        shipment.get(
                            "status",
                            "N/A"
                        )
                    )

                with col3:

                    st.write(
                        "**Origin**"
                    )

                    st.write(
                        shipment.get(
                            "origin",
                            "N/A"
                        )
                    )

                with col4:

                    st.write(
                        "**Destination**"
                    )

                    st.write(
                        shipment.get(
                            "destination",
                            "N/A"
                        )
                    )


    st.divider()


    # -----------------------------------------------------
    # Refresh
    # -----------------------------------------------------

    if st.button(
        "🔄 Refresh Dashboard",
        type="primary"
    ):

        st.rerun()


except requests.exceptions.ConnectionError:

    st.error(
        "❌ Cannot connect to the Shipment Service."
    )

    st.warning(
        "Make sure your Spring Boot Shipment Service "
        "is running on http://localhost:8081"
    )


except requests.exceptions.Timeout:

    st.error(
        "⏱️ The Shipment Service took too long to respond."
    )


except requests.exceptions.HTTPError as error:

    st.error(
        f"❌ Shipment Service returned an HTTP error: {error}"
    )


except Exception as error:

    st.error(
        f"❌ Unable to load dashboard: {error}"
    )