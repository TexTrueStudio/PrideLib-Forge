package io.github.queerbric.flagslib;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FlagsLibFlags {
	private static List<FlagsLibFlag> flags = new ArrayList<>();
	private static Map<String, FlagsLibFlag> flagsById = new Object2ObjectOpenHashMap<>();
	private static final Random DEFAULT_RANDOM = new Random();

	// use Locale.ENGLISH to ensure we get a Gregorian calendar
	private static final boolean PRIDE_MONTH = Calendar.getInstance(Locale.ENGLISH).get(Calendar.MONTH) == Calendar.JUNE
			|| Boolean.getBoolean("everyMonthIsPrideMonth");

	protected static void setFlags(List<FlagsLibFlag> flags) {
		FlagsLibFlags.flags = Collections.unmodifiableList(flags);
		var flagsById = new Object2ObjectOpenHashMap<String, FlagsLibFlag>(flags.size());
		for (var flag : flags) {
			flagsById.put(flag.getId(), flag);
		}
		FlagsLibFlags.flagsById = Collections.unmodifiableMap(flagsById);
	}

	public static List<FlagsLibFlag> getFlags() {
		return flags;
	}

	public static @Nullable FlagsLibFlag getFlag(String id) {
		return flagsById.get(id);
	}

	public static @Nullable FlagsLibFlag getRandomFlag(Random random) {
		if (flags.isEmpty()) return null;
		return flags.get(random.nextInt(flags.size()));
	}

	public static @Nullable FlagsLibFlag getRandomFlag() {
		return getRandomFlag(DEFAULT_RANDOM);
	}

	public static boolean isPrideMonth() {
		return PRIDE_MONTH;
	}
}
