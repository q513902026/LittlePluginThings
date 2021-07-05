package me.hope.lpt.module.sub;

import com.mojang.authlib.GameProfile;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.UUID;
import me.hope.lpt.LittlePluginThings;
import me.hope.lpt.core.ISubMod;
import me.hope.lpt.util.ClassUtil;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

public enum ForgeSubMod implements ISubMod {
  INSTANCE;
  
  private static WeakReference<Object> fakePlayer;
  
  private static GameProfile gameProfile;
  
  private MethodHandle getFakePlayerHandler;
  
  private boolean isLoader;
  
  ForgeSubMod() {
    this.isLoader = false;
  }
  
  public void init() {
    try {
      ClassUtil.forName("net.minecraftforge.fml.common.Loader");
    } catch (Exception e) {
      this.isLoader = false;
      return;
    } 
    LittlePluginThings.getInstance().getLogger().info("[ForgeSubMod]:检测到FML,正在检查Forge Mod 的信息");
    load();
  }
  
  private void load() {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    try {
      LittlePluginThings.getInstance().getLogger().info("[ForgeSubMod]: 正在获取需要的类");
      Class<?> FakePlayerCls = ClassUtil.forName("net.minecraftforge.common.util.FakePlayer");
      Class<?> WorldServerCls = ClassUtil.forName("net.minecraft.world.WorldServer");
      Class<?> FakePlayerFactoryCls = ClassUtil.forName("net.minecraftforge.common.util.FakePlayerFactory");
      LittlePluginThings.getInstance().getLogger().info("[ForgeSubMod]: 正在获取需要的方法");
      MethodType get = MethodType.methodType(FakePlayerCls, WorldServerCls, new Class[] { GameProfile.class });
      this.getFakePlayerHandler = lookup.findStatic(FakePlayerFactoryCls, "get", get);
    } catch (ClassNotFoundException|NoSuchMethodException|IllegalAccessException e) {
      e.printStackTrace();
    } 
    this.isLoader = true;
    gameProfile = new GameProfile(UUID.fromString("bf1b4497-fc50-4e6b-935b-02cd53dc0daa"), "[萌澄果]");
    fakePlayer = new WeakReference(null);
    LittlePluginThings.getInstance().getLogger().info("[ForgeSubMod]: 加载完成");
  }
  
  public WeakReference<Object> getFakePlayer(World world) {
    if (!this.isLoader)
      return new WeakReference(null); 
    if (fakePlayer.get() == null) {
      try {
        fakePlayer = new WeakReference(this.getFakePlayerHandler.invoke(((CraftWorld)world).getHandle(), gameProfile));
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      } 
    } else {
      Object fakePlayerObj = fakePlayer.get();
      try {
        assert fakePlayerObj != null;
        Field worldObjField = fakePlayerObj.getClass().getDeclaredField("worldObj");
        worldObjField.set(fakePlayerObj, ((CraftWorld)world).getHandle());
      } catch (NoSuchFieldException|IllegalAccessException e) {
        e.printStackTrace();
      } 
    } 
    return fakePlayer;
  }
  
  public boolean isLoader() {
    return this.isLoader;
  }
}
