package ru.feytox.etherology.particle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;

public class ItemMovingParticle extends OldMovingParticle {
    private final float sampleU;
    private final float sampleV;

    public ItemMovingParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i,
                              ItemStack stack) {
        super(clientWorld, d, e, f, g, h, i);
        this.setSprite(MinecraftClient.getInstance().getItemRenderer().getModel(stack, world, null, 0)
                .getParticleSprite());
        this.scale /= 5.0f;
        this.sampleU = this.random.nextFloat() * 3.0F;
        this.sampleV = this.random.nextFloat() * 3.0F;
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
