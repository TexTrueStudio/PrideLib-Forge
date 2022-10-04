package io.github.queerbric.flagslib;

//import net.fakefabricmc.api.ClientModInitializer;
import net.fakefabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fakefabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(FlagsLibClient.MODID)
public class FlagsLibClient {

	public static Logger LOGGER = LogManager.getLogger("FlagsLib-Forge");

	public static final String MODID = "flagslib";
	public FlagsLibClient() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInitializeClient);

		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

	}

	public void onInitializeClient(final FMLClientSetupEvent event) {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new FlagsLibLoader());
	}

	public static Logger logger() {
		if (LOGGER == null) {
			LOGGER = LogManager.getLogger("FlagsLib-Forge");
		}

		return LOGGER;
	}
	//@Override
	//public void onInitializeClient() {
	//	ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener((IdentifiableResourceReloadListener) new FlagsLibLoader());
	//}

	//@Override
	//public void onInitialize() {
	//
	//}
}