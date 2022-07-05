//
//  iosTestAppApp.swift
//  iosTestApp
//
//  Created by Ryan Scott on 7/3/22.
//

import SwiftUI

@main
struct iosTestAppApp: App {
    @UIApplicationDelegateAdaptor(IosTestAppDelegate.self) var appDelegate
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
