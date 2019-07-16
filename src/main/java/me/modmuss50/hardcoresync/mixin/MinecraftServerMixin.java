package me.modmuss50.hardcoresync.mixin;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

	@Shadow public abstract int getTicks();

	@Shadow public abstract PlayerManager getPlayerManager();

	@Shadow public abstract ServerAdvancementLoader getAdvancementManager();

	@Inject(at = @At("RETURN"), method = "tick")
	private void tick(BooleanSupplier booleanSupplier, CallbackInfo info){

		//Not the greatest code but works
		if(getTicks() % 100 == 0){
			List<ServerPlayerEntity> players = getPlayerManager().getPlayerList();
			for(Advancement advancement : getAdvancementManager().getAdvancements()){
				for(ServerPlayerEntity entity : players){
					AdvancementProgress advancementProgress = entity.getAdvancementManager().getProgress(advancement);
					Iterator<String> obtained =  advancementProgress.getObtainedCriteria().iterator();

					while(obtained.hasNext()){
						String criterion = obtained.next();
						for(ServerPlayerEntity otherPlayers : players){
							if(otherPlayers.getUuid().equals(entity.getUuid())){
								continue;
							}
							otherPlayers.getAdvancementManager().grantCriterion(advancement, criterion);
						}
					}
				}
			}
		}

	}

}
