//
//  ContentView.swift
//  iosTestApp
//
//  Created by Ryan Scott on 7/3/22.
//

import SwiftUI

import fslogging

struct ContentView: View {
    @State private var addedAttrs = [String]()
    @State private var currentDevMetricOperation = 0
    
    var body: some View {
        VStack(content: {
            Button("Log Analytic", action: {
                FSEventLog.shared.addEvent(eventName: "event_name", attrs: ["key1":"val1", "key2":"val2"])
            })
            Button("Add Random attr", action: {
                let attrName = UUID().uuidString
                let attrValue = UUID().uuidString
                FSEventLog.shared.addAttr(attrName: attrName, attrValue: attrValue)
                addedAttrs.append(attrName)
            })
            Button("Remove Random attr", action: {
                if let toRemove = addedAttrs.randomElement() {
                    FSEventLog.shared.removeAttr(attrName: toRemove)
                    addedAttrs.removeAll(where: { $0 == toRemove })
                }
            })
            Button("Log Info dev metric", action: {
                FSDevMetrics.shared.info(msg: "Info Dev Metric", attrs: ["key1":"info key 1", "key2":"info key 2"])
            })
            Button("Log Watch dev metric", action: {
                FSDevMetrics.shared.watch(msg: "Info Dev Metric", attrs: ["key1":"watch key 1", "key2":"watch key 2"])
            })
            Button(currentDevMetricOperation == 0 ? "Start DevMetrics timed operation" : "End DevMetrics timed operation", action: {
                if (currentDevMetricOperation == 0) {
                    FSDevMetrics.shared.startTimedOperation(operationName: "timedop", operationId: 1)
                    currentDevMetricOperation = 1
                } else {
                    FSDevMetrics.shared.commitTimedOperation(operationName: "timedop", operationId: 1)
                    currentDevMetricOperation = 0
                }
            })
        })
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
        
    }
}
