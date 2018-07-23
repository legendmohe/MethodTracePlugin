package com.legendmohe.plugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.AppExtension;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javassist.ClassPool;
import javassist.NotFoundException;


public class TraceTransform extends Transform {

    private static ClassPool sPool = ClassPool.getDefault();

    private Project mProject;

    private TraceInjector mTraceInjector;

    public TraceTransform(Project p, AppExtension android) {
        this.mProject = p;

        p.getExtensions().create("TraceConfig", TraceConfig.class);

        mTraceInjector = new TraceInjector(mProject, android);
        // 添加android相关class
        try {
            List<File> bootClasspath = android.getBootClasspath();
            for (File file : bootClasspath) {
                Util.log("sPool.appendClassPath:" + file.getAbsolutePath());
                sPool.appendClassPath(file.getAbsolutePath());
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    //transformClassesWith + getName() + For + Debug/Release
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    //需要处理的数据类型，有两种枚举类型
    //CLASSES和RESOURCES，CLASSES代表处理的java的class文件，RESOURCES代表要处理java的资源
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    //指明当前Transform是否支持增量编译
    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        // 添加所有需要的class到pool
        addClassPathToPool(transformInvocation);

        mTraceInjector.onClassPathPrepared();

        // Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
        for (TransformInput input : transformInvocation.getInputs()) {
            //对类型为“文件夹”的input进行遍历
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                //文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等

                // 注入代码
                mTraceInjector.injectDir(directoryInput.getFile().getAbsolutePath(), sPool);

                // 获取output目录
                File dest = transformInvocation.getOutputProvider().getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(),
                        Format.DIRECTORY);

                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.getFile(), dest);
            }
            //对类型为jar文件的input进行遍历
            int i = 0;
            for (JarInput jarInput : input.getJarInputs()) {
                //jar文件一般是第三方依赖库jar文件
//                if (i ++ > 0)
//                    continue;

                // 重命名输出文件（同目录copyFile会冲突）
                String jarName = jarInput.getName();
                String md5Name = DigestUtils.md5Hex(jarInput.getFile().getAbsolutePath());
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4);
                }
                //生成输出路径
                File dest = transformInvocation.getOutputProvider().getContentLocation(jarName + md5Name,
                        jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
                //将输入内容复制到输出
                dest.mkdirs();
                Util.log("isExist:" + dest.getAbsolutePath());
                FileUtils.copyFile(jarInput.getFile(), dest);
            }
        }
    }

    private void addClassPathToPool(TransformInvocation transformInvocation) {
        for (TransformInput input : transformInvocation.getInputs()) {
            //对类型为“文件夹”的input进行遍历
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                //文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等
                try {
                    String absolutePath = directoryInput.getFile().getAbsolutePath();
                    sPool.appendClassPath(absolutePath);
                    Util.log("add class path to pool:" + absolutePath);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
            //对类型为jar文件的input进行遍历
            for (JarInput jarInput : input.getJarInputs()) {
                //jar文件一般是第三方依赖库jar文件
                try {
                    String absolutePath = jarInput.getFile().getAbsolutePath();
                    sPool.appendClassPath(absolutePath);
                    Util.log("add jar path to pool:" + absolutePath);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}