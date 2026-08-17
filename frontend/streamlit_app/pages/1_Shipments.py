import streamlit as st
import requests

from services.shipment_api import get_shipments


st.title("📦 Shipments")

st.write(
    "View shipments from the Shipment Service."
)

st.divider()


status = st.selectbox(
    "Filter by status",
    [
        "ALL",
        "CREATED",
        "PICKED_UP",
        "IN_TRANSIT",
        "OUT_FOR_DELIVERY",
        "DELIVERED",
        "CANCELLED"
    ]
)


if st.button("🔄 Load Shipments"):

    try:

        data = get_shipments(
            page=0,
            size=20,
            status=status
        )

        shipments = data.get(
            "content",
            []
        )

        st.success(
            f"Found {data.get('totalElements', 0)} shipments."
        )

        if not shipments:

            st.info("No shipments found.")

        for shipment in shipments:

            with st.container(border=True):

                col1, col2, col3 = st.columns(3)

                with col1:
                    st.write("**Tracking Number**")
                    st.write(
                        shipment.get(
                            "trackingNumber",
                            "N/A"
                        )
                    )

                with col2:
                    st.write("**Status**")
                    st.write(
                        shipment.get(
                            "status",
                            "N/A"
                        )
                    )

                with col3:
                    st.write("**Route**")
                    st.write(
                        f'{shipment.get("origin", "N/A")} '
                        f'→ '
                        f'{shipment.get("destination", "N/A")}'
                    )

    except requests.exceptions.ConnectionError:

        st.error(
            "Cannot connect to Shipment Service. "
            "Make sure the Java service is running on port 8081."
        )

    except Exception as error:

        st.error(
            f"Unable to retrieve shipments: {error}"
        )