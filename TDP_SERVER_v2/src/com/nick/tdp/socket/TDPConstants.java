package com.nick.tdp.socket;

/**
 * Created by Nick on 2015/7/21.
 */
public class TDPConstants {
    /*Set the Server Address according to actual Server's IP. */
//    public static final String SERVER_ADDRESS_REGISTRATION = "192.168.1.107";
    public static final String SERVER_ADDRESS_REGISTRATION = "localhost";
    public static final int SERVER_PORT_REGISTRATION = 9980;
    public static final int SERVER_PORT_PAIRING = 7750;

    public static final int REGISTRATION_SUCCESS = 11;
    public static final int REGISTRATION_FAILED = 41;
    public static final int AUTHENTICATION_SUCCESS = 12;
    public static final int AUTHENTICATION_FAILED = 42;
    /**
     * For Device Registration
     */
    public static final String PACKET_TYPE = "packet_type_registration";

    /**
     * packet type:
     * for socket communication, packet exchange
     */
    public static final int PACKET_TYPE_REGISTRATION = 101;
    public static final int PACKET_TYPE_REGISTRATION_ACK = 201;
    public static final int PACKET_TYPE_AUTHENTICATION = 102;
    public static final int PACKET_TYPE_AUTHENTICATION_ACK = 202;
    /**
     * For the pay load of packet
     */
    public static final String PACKET_PAYLOAD_REGISTRATION_DEVICE_ID = "registration_device_ID";
    public static final String PACKET_PAYLOAD_REGISTRATION_DEVICE_PUBLIC_KEY = "registration_device_Pub";
    public static final String PACKET_PAYLOAD_REGISTRATION_DEVICE_D = "registration_device_d";
    public static final String PACKET_PAYLOAD_REGISTRATION_DEVICE_R = "registration_device_R";

    public static final String PACKET_PAYLOAD_DEVICE_TRUST_VALUE = "device_trust_value";
    public static final String PACKET_PAYLOAD_MASTER_PUBLIC_KEY = "payload_master_Ppub";
    /**
     * For SharedPreferences - Device Encryption Parameters
     * (ID, x, d, P, R, Pbs, t)
     */
    public static final String SP_DEVICE_ID = "sp_device_id";
    public static final String SP_DEVICE_X = "sp_device_secret_key_x";
    public static final String SP_DEVICE_D = "sp_device_secret_key_d";
    public static final String SP_DEVICE_P = "sp_device_public_key_P";
    public static final String SP_DEVICE_R = "sp_device_public_key_R";
    public static final String SP_MASTER_P = "sp_master_public_key_P";
    public static final String SP_DEVICE_T = "sp_device_trust_value_t";
 }
