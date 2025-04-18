// @ts-nocheck
// This file is generated by Umi automatically
// DO NOT CHANGE IT MANUALLY!
import React from 'react';
import {
  ConfigProvider,
} from 'antd';
import { ApplyPluginsType } from 'umi';
import { getPluginManager } from '../core/plugin';
import { AntdConfigContext, AntdConfigContextSetter } from './context';
import merge from 'E:/Projects/fuu-run/fuu-run-admin/fuu-run-admin/node_modules/lodash/merge'

let cacheAntdConfig = null;

const getAntdConfig = () => {
  if(!cacheAntdConfig){
    cacheAntdConfig = getPluginManager().applyPlugins({
      key: 'antd',
      type: ApplyPluginsType.modify,
      initialValue: {
        ...{"componentSize":"middle","strict":false},
      },
    });
    if (!cacheAntdConfig.theme) {
      cacheAntdConfig.theme = {};
    }
  }
  return cacheAntdConfig;
}

function AntdProvider({ children }) {
  let container = children;

  const [antdConfig, _setAntdConfig] = React.useState(() => {
    const {
      appConfig: _,
      ...finalConfigProvider
    } = getAntdConfig();
    return finalConfigProvider
  });
  const setAntdConfig: typeof _setAntdConfig = (newConfig) => {
    _setAntdConfig(prev => {
      return merge({}, prev, typeof newConfig === 'function' ? newConfig(prev) : newConfig)
    })
  }


  if (antdConfig.prefixCls) {
    ConfigProvider.config({
      prefixCls: antdConfig.prefixCls,
    });
  };

  if (antdConfig.iconPrefixCls) {
    // Icons in message need to set iconPrefixCls via ConfigProvider.config()
    ConfigProvider.config({
      iconPrefixCls: antdConfig.iconPrefixCls,
    });
  };

  if (antdConfig.theme) {
    // Pass config theme to static method
    ConfigProvider.config({
      theme: antdConfig.theme,
    });
  }

  container = <ConfigProvider {...antdConfig}>{container}</ConfigProvider>;


  container = (
    <AntdConfigContextSetter.Provider value={setAntdConfig}>
      <AntdConfigContext.Provider value={antdConfig}>
        {container}
      </AntdConfigContext.Provider>
    </AntdConfigContextSetter.Provider>
  )

  return container;
}

export function rootContainer(children) {
  return (
    <AntdProvider>
      {children}
    </AntdProvider>
  );
}
