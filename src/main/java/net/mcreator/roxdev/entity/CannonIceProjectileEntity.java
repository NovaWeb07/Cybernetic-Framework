package net.mcreator.roxdev.entity;

import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.Explosion;

import net.mcreator.roxdev.init.RoxDevModEntities;

public class CannonIceProjectileEntity extends ThrowableItemProjectile {

    public CannonIceProjectileEntity(PlayMessages.SpawnEntity packet, Level world) {
        super(RoxDevModEntities.CANNON_ICE_PROJECTILE.get(), world);
    }

    public CannonIceProjectileEntity(EntityType<? extends CannonIceProjectileEntity> type, Level world) {
        super(type, world);
    }

    public CannonIceProjectileEntity(Level world, LivingEntity entity) {
        super(RoxDevModEntities.CANNON_ICE_PROJECTILE.get(), entity, world);
    }

    public CannonIceProjectileEntity(Level world, double x, double y, double z) {
        super(RoxDevModEntities.CANNON_ICE_PROJECTILE.get(), x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.BLUE_ICE;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        explode();
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        explode();
    }

    private void explode() {
        if (!this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 2.5F, Level.ExplosionInteraction.MOB);
            this.discard();
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
