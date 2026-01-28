package net.phospherion.kazekatana.block.specialz;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.phospherion.kazekatana.block.entity.ModBlockEntities;
import net.phospherion.kazekatana.block.entity.specialz.TataraFurnaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class TataraFurnaceBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final MapCodec<TataraFurnaceBlock> CODEC = simpleCodec(TataraFurnaceBlock::new);

    public TataraFurnaceBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(LIT, false)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    /* BLOCK ENTITY */

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TataraFurnaceBlockEntity(blockPos, blockState);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof TataraFurnaceBlockEntity tataraBE) {
                tataraBE.drops();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos,
                                              Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof TataraFurnaceBlockEntity tataraBE) {
                ((ServerPlayer) pPlayer).openMenu(new SimpleMenuProvider(tataraBE, Component.literal("TataraFurnace")), pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.TATARAFURNACE_BE.get(),
                (level, blockPos, blockState, blockEntity) -> {
                    if (blockEntity instanceof TataraFurnaceBlockEntity tatara) {
                        tatara.tick(level, blockPos, blockState);
                    }
                });
    }

    /* FACING */

    @Override
    protected BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite()).setValue(LIT, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, LIT);
    }

    /* LIT / PARTICLES */

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }

        double xPos = (double) pos.getX() + 0.5;
        double yPos = pos.getY();
        double zPos = (double) pos.getZ() + 0.5;
        if (random.nextDouble() < 0.15) {
            level.playLocalSound(xPos, yPos, zPos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.5f, 1.5f, false);
        }

        Direction direction = state.getValue(FACING);
        Direction.Axis axis = direction.getAxis();

        double defaultOffset = random.nextDouble() * 0.6 - 0.3;
        double xOffsets = axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52 : defaultOffset;
        double yOffset = random.nextDouble() * 6.0 / 8.0;
        double zOffset = axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52 : defaultOffset;

        // Basic smoke particle
        level.addParticle(ParticleTypes.SMOKE, xPos + xOffsets, yPos + yOffset, zPos + zOffset, 0.0, 0.0, 0.0);

        // If block entity present, spawn item particle for fuel and optionally heat-based particles
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TataraFurnaceBlockEntity tataraBE) {
            ItemStack fuel = tataraBE.itemHandler.getStackInSlot(1);
            if (!fuel.isEmpty()) {
                level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, fuel),
                        xPos + xOffsets, yPos + yOffset, zPos + zOffset, 0.0, 0.0, 0.0);
            }

            // heat-based extra particles (client-side only)
            int heat = tataraBE.getHeatLevel();
            if (heat > 600 && heat < 1500 && random.nextDouble() < 0.12) {
                // sparks when hot
                level.addParticle(ParticleTypes.FLAME, xPos + xOffsets, yPos + yOffset + 0.2, zPos + zOffset, 0.0, 0.02, 0.0);
                level.addParticle(ParticleTypes.FLAME, xPos + xOffsets, yPos + yOffset + 0.4, zPos + zOffset, 0.1, 0.03, 0.0);
                level.addParticle(ParticleTypes.FLAME, xPos + xOffsets, yPos + yOffset + 0.2, zPos + zOffset, 0.0, 0.12, 0.05);
            }
            if (heat > 1500 && random.nextDouble() < 0.16) {
                // bluish sparks / extra smoke when very hot
                level.addParticle(ParticleTypes.END_ROD, xPos + xOffsets, yPos + yOffset + 0.25, zPos + zOffset, 0.0, 0.01, 0.0);
                level.addParticle(ParticleTypes.END_ROD, xPos + xOffsets, yPos + yOffset + 0.25, zPos + zOffset, 0.0, 0.02, 0.01);
            }
        }
    }
}
