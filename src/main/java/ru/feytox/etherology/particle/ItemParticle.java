package ru.feytox.etherology.particle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.effects.ItemParticleEffect;
import ru.feytox.etherology.particle.utility.MovingParticle;

public class ItemParticle extends MovingParticle<ItemParticleEffect> {

    private final float sampleU;
    private final float sampleV;
    private final Vec3d endPos;

    public ItemParticle(ClientWorld clientWorld, double x, double y, double z, ItemParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);

        endPos = parameters.getMoveVec();
        Identifier itemId = new Identifier(parameters.getItemId());
        ItemStack stack = Registries.ITEM.get(itemId).getDefaultStack();

        setSprite(MinecraftClient.getInstance().getItemRenderer().getModel(stack, world, null, 0)
                .getParticleSprite());

        this.scale /= 5.0f;
        this.sampleU = this.random.nextFloat() * 3.0F;
        this.sampleV = this.random.nextFloat() * 3.0F;
        this.maxAge = 80;
    }

    @Override
    public void tick() {
        acceleratedMovingTick(0.4f, 0.5f, true, endPos);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.TERRAIN_SHEET;
    }

    @Override
    protected float getMinU() {
        return this.sprite.getFrameU((this.sampleU + 1.0F) / 4.0F * 16.0F);
    }

    @Override
    protected float getMaxU() {
        return this.sprite.getFrameU(this.sampleU / 4.0F * 16.0F);
    }

    @Override
    protected float getMinV() {
        return this.sprite.getFrameV(this.sampleV / 4.0F * 16.0F);
    }

    @Override
    protected float getMaxV() {
        return this.sprite.getFrameV((this.sampleV + 1.0F) / 4.0F * 16.0F);
    }
}
