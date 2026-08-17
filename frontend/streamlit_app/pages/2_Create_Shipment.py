import streamlit as st
import requests

from services.shipment_api import create_shipment


st.title("➕ Create Shipment")

st.write(
    "Create a new shipment using the Shipment Service."
)

st.divider()


customer_id = st.text_input(
    "Customer ID",
    placeholder="Enter customer UUID"
)

origin = st.text_input(
    "Origin",
    placeholder="e.g. Bangalore"
)

destination = st.text_input(
    "Destination",
    placeholder="e.g. Mumbai"
)


if st.button(
    "Create Shipment",
    type="primary"
):

    if not customer_id:
        st.error("Customer ID is required.")

    elif not origin:
        st.error("Origin is required.")

    elif not destination:
        st.error("Destination is required.")

    else:

        try:

            shipment = create_shipment(
                customer_id,
                origin,
                destination
            )

            st.success(
                "Shipment created successfully!"
            )

            st.subheader("Shipment Details")

            col1, col2 = st.columns(2)

            with col1:

                st.write("**Tracking Number**")

                st.write(
                    shipment.get(
                        "trackingNumber",
                        "N/A"
                    )
                )

                st.write("**Status**")

                st.write(
                    shipment.get(
                        "status",
                        "N/A"
                    )
                )

            with col2:

                st.write("**Origin**")

                st.write(
                    shipment.get(
                        "origin",
                        "N/A"
                    )
                )

                st.write("**Destination**")

                st.write(
                    shipment.get(
                        "destination",
                        "N/A"
                    )
                )

        except requests.exceptions.ConnectionError:

            st.error(
                "Cannot connect to Shipment Service. "
                "Make sure the Java service is running on port 8081."
            )

        except requests.exceptions.HTTPError as error:

            st.error(
                f"Shipment Service rejected the request: {error}"
            )

        except Exception as error:

            st.error(
                f"Failed to create shipment: {error}"
            )