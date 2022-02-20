package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.armor;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.cit.ActiveCITs;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeArmor;

import java.util.Map;

import static shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeArmor.CONTAINER;

@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {
    private Map<String, Identifier> citresewn$cachedTextures = null;

    @Inject(method = "renderArmor", at = @At("HEAD"))
    public void citresewn$renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        citresewn$cachedTextures = null;
        if (!CITResewnConfig.INSTANCE.enabled || !ActiveCITs.isActive())
            return;

        ItemStack equippedStack = entity.getEquippedStack(armorSlot);

        CIT<TypeArmor> cit = CONTAINER.getCIT(new CITContext(equippedStack, entity.getWorld(), entity));
        if (cit != null)
            citresewn$cachedTextures = cit.type.textures;
    }

    @Inject(method = "getArmorTexture", cancellable = true, at = @At("HEAD"))
    private void citresewn$getArmorTexture(ArmorItem item, boolean legs, String overlay, CallbackInfoReturnable<Identifier> cir) {
        if (citresewn$cachedTextures == null)
            return;

        Identifier identifier = citresewn$cachedTextures.get(item.getMaterial().getName() + "_layer_" + (legs ? "2" : "1") + (overlay == null ? "" : "_" + overlay));
        if (identifier != null)
            cir.setReturnValue(identifier);
    }
}
