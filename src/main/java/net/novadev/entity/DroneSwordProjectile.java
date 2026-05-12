package net.novadev.entity;

import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

import net.novadev.init.NovaDevModEntities;

public class DroneSwordProjectile extends ThrowableProjectile {

    private static final float DAMAGE = 6.0f;

    public DroneSwordProjectile(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
    }

    public DroneSwordProjectile(Level level, LivingEntity shooter) {
        super(NovaDevModEntities.DRONE_SWORD.get(), shooter, level);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level() instanceof ServerLevel sl && this.tickCount % 2 == 0) {
            sl.sendParticles(ParticleTypes.CRIT,
                    this.getX(), this.getY(), this.getZ(),
                    2, 0.05, 0.05, 0.05, 0.01);
            sl.sendParticles(ParticleTypes.FLAME,
                    this.getX(), this.getY(), this.getZ(),
                    1, 0.03, 0.03, 0.03, 0.005);
        }

        if (this.tickCount > 100) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity hit = result.getEntity();

        if (!this.level().isClientSide() && hit instanceof LivingEntity target) {
            Entity shooter = this.getOwner();
            if (shooter instanceof FlamdroneEntity drone) {
                if (target == drone) return;
                if (target instanceof net.minecraft.world.entity.player.Player p
                        && p.getUUID().equals(drone.getOwnerUUID())) {
                    return;
                }
            }

            DamageSource source;
            if (shooter != null) {
                source = this.damageSources().mobProjectile(this, (LivingEntity) shooter);
            } else {
                source = this.damageSources().magic();
            }
            target.hurt(source, DAMAGE);
            target.setSecondsOnFire(3);

            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.CRIT,
                        target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                        15, 0.3, 0.3, 0.3, 0.1);
                sl.sendParticles(ParticleTypes.FLAME,
                        target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                        8, 0.2, 0.2, 0.2, 0.05);
            }

            this.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.NEUTRAL, 1.0f, 1.2f);

            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ANVIL_LAND, SoundSource.NEUTRAL, 0.3f, 1.8f);

            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        this.getX(), this.getY(), this.getZ(),
                        10, 0.1, 0.1, 0.1, 0.1);
            }

            this.discard();
        }
    }

    @Override
    protected float getGravity() {
        return 0.01f;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
