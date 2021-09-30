package com.dat3m.dartagnan;

import com.dat3m.dartagnan.analysis.saturation.SaturationSolver;
import com.dat3m.dartagnan.analysis.saturation.util.Learner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GlobalSettings {
	
	private static final Logger logger = LogManager.getLogger(GlobalSettings.class);

    // === Parsing ===
    public static final boolean ATOMIC_AS_LOCK = false;

    // === WMM Assumptions ===
    public static final boolean ASSUME_LOCAL_CONSISTENCY = true;
    public static final boolean PERFORM_ATOMIC_BLOCK_OPTIMIZATION = true;

    // === Encoding ===
    public static final boolean FIXED_MEMORY_ENCODING = false;
    // NOTE: ALLOW_PARTIAL_MODELS does NOT work on Litmus tests due to their different assertion condition
    //TODO: This is not used right now. Some previous merge with the JavaSMT branch removed its usage
    // We will fix this later or remove this option completely.
    public static final boolean ALLOW_PARTIAL_MODELS = false;
    public static final boolean MERGE_CF_VARS = true; // ONLY has effect if ALLOW_PARTIAL_MODELS is 'false'
    public static final boolean ANTISYMM_CO = false;
    public static final boolean ENABLE_SYMMETRY_BREAKING = false;

    // === BranchEquivalence ===
    public static final boolean MERGE_BRANCHES = true;
    public static final boolean ALWAYS_SPLIT_ON_JUMP = false;

    // === Static analysis ===
    public static final boolean PERFORM_DEAD_CODE_ELIMINATION = true;
    public static final boolean PERFORM_REORDERING = true;
    public static final boolean DETERMINISTIC_REORDERING = true;
    public static final boolean ENABLE_SYMMETRY_REDUCTION = false;

    // ==== Refinement ====
    public static final boolean REFINEMENT_ENCODE_COHERENCE = true;
    public static final boolean REFINEMENT_USE_LOCALLY_CONSISTENT_BASELINE_WMM = false; // Uses acyclic(po-loc + rf) as baseline
    // Changes the above flag to mean acyclic (por-loc | rf | co). Only takes effect if the above flag as well as
    // REFINEMENT_ENCODE_COHERENCE are set.
    public static final boolean REFINEMENT_ADD_COHERENCE_TO_LOCAL_CONSISTENCY = false;
    public static final boolean REFINEMENT_USE_ACYCLIC_DEP_RF_BASELINE_WMM = false; // Uses acyclic(rf | dep)

    public enum SymmetryLearning { NONE, LINEAR, QUADRATIC, FULL }
    public static final SymmetryLearning REFINEMENT_SYMMETRY_LEARNING = SymmetryLearning.NONE;

    // ==== Saturation ====
    public static boolean SATURATION_ENABLE_DEBUG = false;
    public static SaturationSolver.Mode SATURATION_MODE = SaturationSolver.Mode.MODEL_CHECKING;
    // NOTE: This setting only has an effect if <REFINEMENT_ENCODE_COHERENCES> is enabled.
    public static boolean SATURATION_USE_MODEL_COHERENCES = true;
    public static boolean SATURATION_REDUCE_REASONS_TO_CORE_REASONS = true;
    // NOTE: This setting only has an effect if <REFINEMENT_ENCODE_COHERENCES> is enabled.
    public static boolean SATURATION_NO_RESOLUTION = false;
    public static final int SATURATION_MAX_DEPTH = 3;

    public static final Learner.ViolationLearningStrategy SATURATION_VIOLATION_LEARNING = Learner.ViolationLearningStrategy.NONE;

    // --------------------

    // === Recursion depth ===
    public static final int MAX_RECURSION_DEPTH = 200;

    // === Debug ===
    public static final boolean ENABLE_DEBUG_OUTPUT = false;

    public static void LogGlobalSettings() {
        // General settings
    	logger.info("ATOMIC_AS_LOCK: " + ATOMIC_AS_LOCK);
    	logger.info("ASSUME_LOCAL_CONSISTENCY: " + ASSUME_LOCAL_CONSISTENCY);
    	logger.info("PERFORM_ATOMIC_BLOCK_OPTIMIZATION: " + PERFORM_ATOMIC_BLOCK_OPTIMIZATION);
    	logger.info("MERGE_CF_VARS: " + MERGE_CF_VARS);
    	logger.info("ALLOW_PARTIAL_MODELS: " + ALLOW_PARTIAL_MODELS);
    	logger.info("ANTISYMM_CO: " + ANTISYMM_CO);
    	logger.info("MERGE_BRANCHES: " + MERGE_BRANCHES);
    	logger.info("ALWAYS_SPLIT_ON_JUMP: " + ALWAYS_SPLIT_ON_JUMP);
    	logger.info("PERFORM_DEAD_CODE_ELIMINATION: " + PERFORM_DEAD_CODE_ELIMINATION);
    	logger.info("PERFORM_REORDERING: " + PERFORM_REORDERING);
    	logger.info("ENABLE_SYMMETRY_BREAKING: " + ENABLE_SYMMETRY_BREAKING);
        logger.info("ENABLE_SYMMETRY_REDUCTION: " + ENABLE_SYMMETRY_REDUCTION);
    	logger.info("MAX_RECURSION_DEPTH: " + MAX_RECURSION_DEPTH);
    	logger.info("ENABLE_DEBUG_OUTPUT: " + ENABLE_DEBUG_OUTPUT);

    	// Refinement settings
    	logger.info("REFINEMENT_USE_LOCALLY_CONSISTENT_BASELINE_WMM: " + REFINEMENT_USE_LOCALLY_CONSISTENT_BASELINE_WMM);
    	logger.info("REFINEMENT_ADD_ACYCLIC_DEP_RF: " + REFINEMENT_USE_ACYCLIC_DEP_RF_BASELINE_WMM);
    	logger.info("REFINEMENT_SYMMETRY_LEARNING: " + REFINEMENT_SYMMETRY_LEARNING.name());

    	// Saturation settings
        logger.info("SATURATION_ENABLE_DEBUG: " + SATURATION_ENABLE_DEBUG);
        logger.info("SATURATION_MAX_DEPTH: " + SATURATION_MAX_DEPTH);
    }
}
