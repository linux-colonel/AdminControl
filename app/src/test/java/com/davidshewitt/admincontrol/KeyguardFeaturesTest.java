package com.davidshewitt.admincontrol;

import android.app.admin.DevicePolicyManager;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for testing keyguard disabled features logic.
 */
public class KeyguardFeaturesTest {
    /**
     * No keyguard features disabled, no request to disable fingerprint.
     * @throws Exception any failure.
     */
    @Test
    public void test_noChangeNoKeyguardFeaturesDisabled() throws Exception {
        int result = KeyguardFeatures.setFingerprintDisabled(
                DevicePolicyManager.KEYGUARD_DISABLE_FEATURES_NONE, false);
        assertEquals(DevicePolicyManager.KEYGUARD_DISABLE_FEATURES_NONE, result);
    }

    /**
     * No keyguard features disabled, request to disable fingerprint.
     * @throws Exception any failure.
     */
    @Test
    public void test_changeNoKeyguardFeaturesDisabled() throws Exception {
        int result = KeyguardFeatures.setFingerprintDisabled(
                DevicePolicyManager.KEYGUARD_DISABLE_FEATURES_NONE, true);
        assertEquals(DevicePolicyManager.KEYGUARD_DISABLE_FINGERPRINT, result);
    }

    /**
     * One keyguard features disabled, no request to disable fingerprint.
     * @throws Exception any failure.
     */
    @Test
    public void test_noChangeOneKeyguardFeaturesDisabled() throws Exception {
        int result = KeyguardFeatures.setFingerprintDisabled(
                DevicePolicyManager.KEYGUARD_DISABLE_TRUST_AGENTS, false);
        assertEquals(DevicePolicyManager.KEYGUARD_DISABLE_TRUST_AGENTS, result);
    }

    /**
     * One keyguard features disabled, request to disable fingerprint.
     * @throws Exception any failure.
     */
    @Test
    public void test_changeOneKeyguardFeaturesDisabled() throws Exception {
        int expected = DevicePolicyManager.KEYGUARD_DISABLE_TRUST_AGENTS
                + DevicePolicyManager.KEYGUARD_DISABLE_FINGERPRINT;
        int result = KeyguardFeatures.setFingerprintDisabled(
                DevicePolicyManager.KEYGUARD_DISABLE_TRUST_AGENTS, true);
        assertEquals(expected, result);
    }

    /**
     * All keyguard features disabled, request to disable fingerprint.
     * @throws Exception any failure.
     */
    @Test
    public void test_changeAllKeyguardFeaturesDisabled() throws Exception {
        int expected = DevicePolicyManager.KEYGUARD_DISABLE_FEATURES_ALL;
        int result = KeyguardFeatures.setFingerprintDisabled(
                DevicePolicyManager.KEYGUARD_DISABLE_FEATURES_ALL, true);
        assertEquals(expected, result);
    }
}