package io.github.queerbric.pride;

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

@Mod(PrideClient.MODID)
public class PrideClient {

	public static Logger LOGGER = LogManager.getLogger("PrideLib-Forge");

	public static final String MODID = "pridelib";
	public PrideClient() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInitializeClient);

		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

	}

	public void onInitializeClient(final FMLClientSetupEvent event) {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener((IdentifiableResourceReloadListener) new PrideLoader());
	}

	public static Logger logger() {
		if (LOGGER == null) {
			LOGGER = LogManager.getLogger("Forbric API");
		}

		return LOGGER;
	}
	//@Override
	//public void onInitializeClient() {
	//	ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener((IdentifiableResourceReloadListener) new PrideLoader());
	//}

	//@Override
	//public void onInitialize() {
	//
	//}
}