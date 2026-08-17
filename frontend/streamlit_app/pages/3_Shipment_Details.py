import streamlit as st
import requests

from services.shipment_api import (
    get_shipment,
    get_shipment_by_tracking_number,
    get_shipments_by_customer,
    update_shipment_status,
    get_shipment_history
)


st.title("🔎 Shipment Search")

st.write(
    "Search for shipments using Tracking Number, Shipment ID, "
    "or Customer ID."
)

st.divider()


# ---------------------------------------------------------
# Session state
# ---------------------------------------------------------

if "search_results" not in st.session_state:
    st.session_state.search_results = []

if "has_searched" not in st.session_state:
    st.session_state.has_searched = False


# ---------------------------------------------------------
# Search type
# ---------------------------------------------------------

search_type = st.radio(
    "Search using",
    [
        "Tracking Number",
        "Shipment ID",
        "Customer ID"
    ],
    horizontal=True
)


# ---------------------------------------------------------
# Input
# ---------------------------------------------------------

if search_type == "Tracking Number":

    placeholder = "e.g. SHP-9E8E7F34DF41"

elif search_type == "Shipment ID":

    placeholder = "Enter shipment UUID"

else:

    placeholder = "Enter customer UUID"


search_value = st.text_input(
    search_type,
    placeholder=placeholder
)


# ---------------------------------------------------------
# Search button
# ---------------------------------------------------------

if st.button(
    "🔍 Search",
    type="primary"
):

    if not search_value.strip():

        st.error(
            f"{search_type} is required."
        )

        st.session_state.search_results = []
        st.session_state.has_searched = False

    else:

        try:

            if search_type == "Tracking Number":

                result = get_shipment_by_tracking_number(
                    search_value.strip()
                )

                shipments = [result]

            elif search_type == "Shipment ID":

                result = get_shipment(
                    search_value.strip()
                )

                shipments = [result]

            else:

                shipments = get_shipments_by_customer(
                    search_value.strip()
                )


            st.session_state.search_results = shipments
            st.session_state.has_searched = True


        except requests.exceptions.ConnectionError:

            st.error(
                "Cannot connect to Shipment Service. "
                "Make sure the Java service is running "
                "on port 8081."
            )

            st.session_state.search_results = []
            st.session_state.has_searched = False


        except requests.exceptions.HTTPError:

            st.error(
                "The Shipment Service could not find "
                "the requested shipment."
            )

            st.session_state.search_results = []
            st.session_state.has_searched = False


        except Exception as error:

            st.error(
                f"Unable to search shipments: {error}"
            )

            st.session_state.search_results = []
            st.session_state.has_searched = False


# ---------------------------------------------------------
# Display search results
# ---------------------------------------------------------

if st.session_state.has_searched:

    shipments = st.session_state.search_results


    if not shipments:

        st.info(
            "No shipments found for this search."
        )


    else:

        st.success(
            f"Found {len(shipments)} shipment(s)."
        )


        for shipment in shipments:

            shipment_id = shipment.get(
                "id",
                ""
            )

            tracking_number = shipment.get(
                "trackingNumber",
                "N/A"
            )

            current_status = shipment.get(
                "status",
                "CREATED"
            )


            with st.container(border=True):

                # -----------------------------------------
                # Shipment heading
                # -----------------------------------------

                st.subheader(
                    f"📦 {tracking_number}"
                )


                # -----------------------------------------
                # Shipment information
                # -----------------------------------------

                col1, col2, col3 = st.columns(3)


                with col1:

                    st.write("**Shipment ID**")

                    st.write(shipment_id)

                    st.write("**Customer ID**")

                    st.write(
                        shipment.get(
                            "customerId",
                            "N/A"
                        )
                    )


                with col2:

                    st.write("**Status**")

                    st.write(current_status)

                    st.write("**Origin**")

                    st.write(
                        shipment.get(
                            "origin",
                            "N/A"
                        )
                    )


                with col3:

                    st.write("**Destination**")

                    st.write(
                        shipment.get(
                            "destination",
                            "N/A"
                        )
                    )

                    st.write(
                        "**Estimated Delivery**"
                    )

                    estimated = shipment.get(
                        "estimatedDelivery"
                    )

                    st.write(
                        estimated
                        if estimated
                        else "Not available"
                    )


                # -----------------------------------------
                # Timestamps
                # -----------------------------------------

                st.divider()

                st.write("### 🕒 Timestamps")

                time_col1, time_col2 = st.columns(2)


                with time_col1:

                    st.write("**Created At**")

                    st.write(
                        shipment.get(
                            "createdAt",
                            "N/A"
                        )
                    )


                with time_col2:

                    st.write("**Updated At**")

                    st.write(
                        shipment.get(
                            "updatedAt",
                            "N/A"
                        )
                    )


                # -----------------------------------------
                # Update Status
                # -----------------------------------------

                st.divider()

                st.write("### 🔄 Update Status")


                status_options = [
                    "CREATED",
                    "PICKED_UP",
                    "IN_TRANSIT",
                    "OUT_FOR_DELIVERY",
                    "DELIVERED",
                    "CANCELLED"
                ]


                if current_status in status_options:

                    current_index = (
                        status_options.index(
                            current_status
                        )
                    )

                else:

                    current_index = 0


                new_status = st.selectbox(
                    "New Status",
                    status_options,
                    index=current_index,
                    key=f"status_{shipment_id}"
                )


                if st.button(
                    "Update Status",
                    key=f"update_{shipment_id}"
                ):

                    if new_status == current_status:

                        st.warning(
                            f"Shipment is already "
                            f"{current_status}."
                        )

                    else:

                        try:

                            updated_shipment = (
                                update_shipment_status(
                                    shipment_id,
                                    new_status
                                )
                            )


                            for index, stored_shipment in enumerate(
                                st.session_state.search_results
                            ):

                                if (
                                    stored_shipment.get("id")
                                    == shipment_id
                                ):

                                    st.session_state.search_results[
                                        index
                                    ] = updated_shipment


                            st.success(
                                "Status updated successfully."
                            )

                            st.rerun()


                        except requests.exceptions.HTTPError as error:

                            st.error(
                                "The Shipment Service rejected "
                                "the status update."
                            )

                            st.code(str(error))


                        except requests.exceptions.ConnectionError:

                            st.error(
                                "Cannot connect to Shipment Service."
                            )


                        except Exception as error:

                            st.error(
                                f"Unable to update status: {error}"
                            )


                # -----------------------------------------
                # Shipment History
                # -----------------------------------------

                st.divider()

                st.write("### 📜 Shipment History")


                if st.button(
                    "View History",
                    key=f"history_{shipment_id}"
                ):

                    try:

                        history = get_shipment_history(
                            shipment_id
                        )


                        if not history:

                            st.info(
                                "No shipment history available."
                            )


                        else:

                            st.write(
                                f"**{len(history)} history event(s)**"
                            )


                            for event in history:

                                old_status = event.get(
                                    "oldStatus",
                                    "—"
                                )

                                new_status = event.get(
                                    "newStatus",
                                    "—"
                                )

                                changed_at = event.get(
                                    "changedAt",
                                    event.get(
                                        "createdAt",
                                        "Unknown time"
                                    )
                                )


                                st.write(
                                    f"**{old_status} → "
                                    f"{new_status}**"
                                )

                                st.caption(
                                    str(changed_at)
                                )

                                st.divider()


                    except requests.exceptions.ConnectionError:

                        st.error(
                            "Cannot connect to Shipment Service."
                        )


                    except requests.exceptions.HTTPError as error:

                        st.error(
                            "Unable to retrieve shipment history."
                        )

                        st.code(str(error))


                    except Exception as error:

                        st.error(
                            f"Unable to load history: {error}"
                        )